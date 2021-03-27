import zlib


def create_check_sum(payload: bytes) -> int:
    return zlib.adler32(payload)


def verify_check_sum(check_sum: int, payload: bytes) -> bool:
    return check_sum == zlib.adler32(payload)
