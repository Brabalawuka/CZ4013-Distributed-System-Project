from utils import marshall, unmarshall
from helpers import UDPClientSocket


def request(data: dict, **kwargs) -> dict:
    marshalled_data = marshall(data)
    raw_data_in_bytes = UDPClientSocket.send_msg(msg=marshalled_data, **kwargs)
    return unmarshall(raw_data_in_bytes)
