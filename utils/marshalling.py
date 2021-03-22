from enum import Enum
from typing import Tuple
import struct
from uuid import uuid4 as uuid


class MessageType(Enum):
    CALL = b'\x00'
    REPLY = b'\x01'
    EXCEPTION = b'\x02'
    ONEWAY = b'\x03'


class ServiceType(Enum):
    FACILITY_NAMELIST_CHECKING = 'FACILITY_NAMELIST_CHECKING'
    FACILITY_AVAIL_CHECKING = 'FACILITY_AVAIL_CHECKING'
    FACILITY_BOOKING = 'FACILITY_BOOKING'
    FACILITY_BOOKING_AMENDMENT = 'FACILITY_BOOKING_AMENDMENT'
    FACILITY_AVAIL_CHECKING_SUBSCRIPTION = 'FACILITY_AVAIL_CHECKING_SUBSCRIPTION'


type_to_hex = dict({
    int: b'\x00',
    float: b'\x01',
    str: b'\x03',
    bool: b'\x04',
    list: b'\x05'
})


class BaseMessage:
    def __init__(self, request_id: str):
        self.request_id = request_id


class CallMessage(BaseMessage):
    def __init__(self, service: ServiceType, data: Tuple):
        super().__init__(str(uuid()))
        self.msg_type = MessageType.CALL
        self.service = service
        self.data = data

    def marshall(self) -> bytearray:
        msg_in_bytes = bytearray(self.msg_type.value)
        msg_in_bytes += bytes(self.request_id.encode('ascii'))
        msg_in_bytes += struct.pack('B', len(self.service.value))
        msg_in_bytes += bytes(self.service.value.encode('ascii'))
        for a in self.data:
            msg_in_bytes += self._serialize_data(a)
        return msg_in_bytes

    @classmethod
    def _serialize_data(cls, a) -> bytes:
        type_a = type(a)
        serialized_form = type_to_hex[type_a]

        # FIXME little-endian problem - need to be consistent with server
        if type_a is int:
            serialized_form += struct.pack('>i', a)
        elif type_a is float:
            serialized_form += struct.pack('>f', a)
        elif type_a is bool:
            serialized_form += struct.pack('>b', a)
        elif type_a is str:
            serialized_form += struct.pack('>i', len(a)) + bytes(a.encode('ascii'))
        elif type_a is list:
            # FIXME not empty array allowed
            inner_type = type(a[0])
            serialized_inner_data = bytearray()
            for inner_a in a:
                serialized_inner_data += cls._serialize_data(inner_a)[1:]  # type is not needed
            serialized_form += type_to_hex[inner_type] + struct.pack('>i', len(a)) + serialized_inner_data

        return serialized_form


class ReplyMessage(BaseMessage):
    def __init__(self, request_id: str, data: list):
        super().__init__(request_id)
        self.msg_type = MessageType.REPLY
        self.data = data


class ExceptionMessage(BaseMessage):
    def __init__(self, request_id: str, error_msg: str):
        super().__init__(request_id)
        self.msg_type = MessageType.EXCEPTION
        self.error_msg = error_msg


class OneWayMessage(ReplyMessage):
    def __init__(self, request_id: str, data: list):
        super().__init__(request_id, data)
        self.msg_type = MessageType.ONEWAY


def unmarshall(data: bytes) -> BaseMessage:
    # TODO
    return ReplyMessage('1', [])


if __name__ == '__main__':
    msg = CallMessage(service=ServiceType.FACILITY_AVAIL_CHECKING, data=('North Hill Gym', ['Sun', 'Coming Mon']))
    bytes_msg = msg.marshall()
    from helpers import UDPClientSocket

    UDPClientSocket.send_msg(msg=bytes_msg, request_id=msg.request_id)
