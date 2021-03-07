from utils import marshall, unmarshall
from helpers import UDPClientSocket
from utils import ServiceType, MessageType


def request(type: MessageType, service: ServiceType, *args, **kwargs) -> dict:
    marshalled_msg = marshall(type, service, *args)
    raw_data_in_bytes = UDPClientSocket.send_msg(msg=marshalled_msg, **kwargs)
    return unmarshall(raw_data_in_bytes)