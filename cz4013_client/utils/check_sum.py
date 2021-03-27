import zlib


def create_check_sum(payload: bytes) -> int:
    check_sum = zlib.adler32(payload)
    print("CheckSum: " + str(check_sum))
    return check_sum


def verify_check_sum(check_sum: int, payload: bytes) -> bool:
    print("Input CheckSum: " + str(check_sum) + "\n")
    print("Created CheckSum: " + str(zlib.adler32(payload)) + "\n")
    return check_sum == zlib.adler32(payload)