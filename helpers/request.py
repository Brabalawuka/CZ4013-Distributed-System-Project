from typing import Callable, Union

from helpers import UDPClientSocket
from utils import ServiceType, CallMessage, BaseMessage, ReplyMessage, OneWayMessage, ExceptionMessage


def notify():
    # TODO: notify server without waiting for response
    pass


def request(service: ServiceType, *args, **kwargs) -> Union[ReplyMessage, OneWayMessage, ExceptionMessage]:
    msg = CallMessage(service=service, data=args)
    marshalled_msg = msg.marshall()
    reply_msg = UDPClientSocket.send_msg(msg=marshalled_msg, request_id=msg.request_id, **kwargs)
    return reply_msg


def listen(func: Callable, **kwargs):
    UDPClientSocket.listen_msg(call_back_function=func, **kwargs)
