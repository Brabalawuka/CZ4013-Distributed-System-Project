import zlib


def create_validation_code(payload: bytes) -> int:
    return zlib.adler32(payload)


def verify_validation_code(stated_validation_code: int, payload: bytes) -> bool:
    return stated_validation_code == zlib.adler32(payload)
