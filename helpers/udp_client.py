import socket
from typing import Union

from configs import *
from utils import print_warning, print_error


class UDPClientSocket:
    UDPSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
    UDPSocket.bind((CLIENT_IP, CLIENT_PORT))
    serverAddressPort = (SERVER_IP, SERVER_PORT)

    @classmethod
    def send_msg(cls, msg: bytes, wait_for_response=True, time_out=5,
                 max_attempt: int = float('inf'), buffer_size=1024) -> Union[bytes, None]:
        if wait_for_response:
            attempt = 0
            while attempt <= max_attempt:
                cls.UDPSocket.settimeout(time_out)
                attempt += 1
                try:
                    cls.UDPSocket.sendto(msg, cls.serverAddressPort)
                    while True:
                        data, addr = cls.UDPSocket.recvfrom(buffer_size)
                        if addr == cls.serverAddressPort:
                            return data
                except socket.timeout:
                    print_warning(msg=f'No Message Received From Server In {time_out} Seconds. Resending...')
            print_error(msg=f'Maximum {max_attempt} Attempts Reached. '
                            f'Please Check Your Internet Connection And Try Again Later.')
        else:
            cls.UDPSocket.sendto(msg, cls.serverAddressPort)
