from enum import Enum
import struct
from uuid import uuid4 as uuid


class MessageType(Enum):
    CALL = b'\x00'
    REPLY = b'\x01'
    EXCEPTION = b'\x02'
    ONEWAY = b'\x03'


class ServiceType(Enum):
    FACILITY_AVAIL_CHECKING = 'FACILITY_AVAIL_CHECKING'


type_to_hex = dict({
    int: b'\x00',
    float: b'\x01',
    str: b'\x03',
    bool: b'\x04',
    list: b'\x05'
})


def marshall(type: MessageType, service: ServiceType, *args) -> bytearray:
    msg_in_bytes = bytearray(type.value)
    request_id = str(uuid())
    msg_in_bytes += bytes(request_id.encode('ascii'))
    msg_in_bytes += struct.pack('B', len(service.value))
    msg_in_bytes += bytes(service.value.encode('ascii'))
    for a in args:
        msg_in_bytes += _serialize_data(a)
    return msg_in_bytes


def _serialize_data(a) -> bytes:
    type_a = type(a)
    serialized_form = type_to_hex[type_a]

    if type_a is int:
        serialized_form += struct.pack('i', a)
    if type_a is float:
        serialized_form += struct.pack('f', a)
    elif type_a is bool:
        serialized_form += struct.pack('b', a)
    elif type_a is str:
        serialized_form += struct.pack('i', len(a)) + bytes(a.encode('ascii'))
    elif type_a is list:
        # FIXME not empty array allowed
        inner_type = type(a[0])
        serialized_inner_data = bytearray()
        for inner_a in a:
            serialized_inner_data += _serialize_data(inner_a)[1:]  # type is not needed
        serialized_form + type_to_hex[inner_type] + struct.pack('i', len(a)) + serialized_inner_data

    return serialized_form


def unmarshall(data: bytes) -> dict:
    # TODO
    pass


if __name__ == '__main__':
    msg = marshall(MessageType.CALL, ServiceType.FACILITY_AVAIL_CHECKING, 'North Hill Gym', ['Sun', 'Coming Mon'])
    from helpers import UDPClientSocket

    UDPClientSocket.send_msg(msg=msg)
