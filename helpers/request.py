from typing import Callable

from utils import unmarshall
from helpers import UDPClientSocket
from utils import ServiceType, MessageType, print_error, CallMessage, BaseMessage


def request(type: MessageType, service: ServiceType, *args, **kwargs) -> BaseMessage:
    msg = CallMessage(service=service, data=args)
    marshalled_msg = msg.marshall()
    raw_data_in_bytes = UDPClientSocket.send_msg(msg=marshalled_msg, **kwargs)
    reply_msg = unmarshall(raw_data_in_bytes)
    return reply_msg


def listen(func: Callable, **kwargs):
    UDPClientSocket.listen_msg(call_back_function=func, **kwargs)
