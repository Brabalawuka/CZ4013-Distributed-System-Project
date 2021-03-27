from typing import Callable, Union
import struct
from helpers import UDPClientSocket
from utils import ServiceType, CallMessage, BaseMessage, ReplyMessage, OneWayMessage, ExceptionMessage, create_check_sum


def notify():
    # TODO: notify server without waiting for response
    pass


def request(service: ServiceType, *args, **kwargs) -> Union[ReplyMessage, OneWayMessage, ExceptionMessage]:
    msg = CallMessage(service=service, data=args)
    marshalled_msg = msg.marshall()
    check_sum = create_check_sum(marshalled_msg)
    print(struct.pack('<I', check_sum))
    marshalled_msg = struct.pack('<I', check_sum) + marshalled_msg
    reply_msg = UDPClientSocket.send_msg(msg=marshalled_msg, request_id=msg.request_id, **kwargs)
    return reply_msg


def listen(func: Callable, **kwargs):
    UDPClientSocket.listen_msg(call_back_function=func, **kwargs)
