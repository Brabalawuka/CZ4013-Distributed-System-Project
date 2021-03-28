from typing import Callable, Union
import struct
from helpers import UDPClientSocket
from utils import ServiceType, CallMessage, ReplyMessage, OneWayMessage, ExceptionMessage, create_validation_code



def request(service: ServiceType, *args, **kwargs) -> Union[ReplyMessage, OneWayMessage, ExceptionMessage]:
    msg = CallMessage(service=service, data=args)
    marshalled_msg = msg.marshall()
    marshalled_msg = struct.pack('<I', create_validation_code(marshalled_msg)) + marshalled_msg
    # print(marshalled_msg)
    reply_msg = UDPClientSocket.send_msg(msg=marshalled_msg, request_id=msg.request_id, **kwargs)
    return reply_msg


def notify(service: ServiceType, request_id: str, *args, **kwargs):
    msg = OneWayMessage(service=service, request_id=request_id, data=args)
    marshalled_msg = msg.marshall()
    marshalled_msg = struct.pack('<I', create_validation_code(marshalled_msg)) + marshalled_msg
    # print(marshalled_msg)
    UDPClientSocket.send_msg(msg=marshalled_msg, request_id=msg.request_id, wait_for_response=False, **kwargs)


def listen(func: Callable, **kwargs):
    UDPClientSocket.listen_msg(call_back_function=func, **kwargs)
