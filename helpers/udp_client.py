from time import time
import socket
from typing import Union
import random

from configs import *
from utils import print_warning, print_error


def get_free_port():
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.bind(("", 0))
    s.listen(1)
    port = s.getsockname()[1]
    s.close()
    return port


class UDPClientSocket:
    UDPSocket = socket.socket(family=socket.AF_INET, type=socket.SOCK_DGRAM)
    UDPSocket.bind((CLIENT_IP, CLIENT_PORT if CLIENT_PORT is not None else get_free_port()))
    serverAddressPort = (SERVER_IP, SERVER_PORT)

    @classmethod
    def send_msg(cls, msg: bytes, wait_for_response: int = True, time_out: int = 5,
                 max_attempt: int = float('inf'), buffer_size: int = 1024,
                 simulate_comm_omission_fail=True) -> Union[bytes, None]:
        if wait_for_response:
            attempt = 0
            while attempt <= max_attempt:
                cls.UDPSocket.settimeout(time_out)
                attempt += 1
                try:
                    if not simulate_comm_omission_fail or\
                            simulate_comm_omission_fail and random.randint(0, 9) != 0:
                        cls.UDPSocket.sendto(msg, cls.serverAddressPort)

                    updated_time_out = time_out
                    while True:
                        start = time()
                        data, addr = cls.UDPSocket.recvfrom(buffer_size)
                        end = time()

                        if addr == cls.serverAddressPort:
                            return data
                        else:
                            updated_time_out -= end - start
                            if updated_time_out <= 0:
                                raise socket.timeout
                            cls.UDPSocket.settimeout(updated_time_out)
                except socket.timeout:
                    print_warning(msg=f'No Message Received From Server In {time_out} Seconds. Resending...')
            print_error(msg=f'Maximum {max_attempt} Attempts Reached. '
                            f'Please Check Your Internet Connection And Try Again Later.')
        else:
            cls.UDPSocket.sendto(msg, cls.serverAddressPort)


if __name__ == '__main__':
    UDPClientSocket.send_msg(msg=b'test')
