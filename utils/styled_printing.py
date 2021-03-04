from typing import List


class Colors:
    HEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKCYAN = '\033[96m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'
    BOLD = '\033[1m'
    UNDERLINE = '\033[4m'


class SpecialCharacters:
    DOT = '\u2022'


def print_options(opts: List[str], ordered=True) -> None:
    for i, opt in enumerate(opts, 1):
        print(f"{Colors.OKCYAN}{f'{i} -' if ordered else SpecialCharacters.DOT}{Colors.ENDC} {opt}")


def print_message(msg: str) -> None:
    print(f"{Colors.OKBLUE}{msg}{Colors.ENDC}")


def print_warning(msg: str) -> None:
    print(f"{Colors.WARNING}{msg}{Colors.ENDC}")


def print_error(msg: str) -> None:
    print(f"{Colors.FAIL}{msg}{Colors.ENDC}")


def prompt_message_decorator(msg: str) -> str:
    return f"{Colors.OKBLUE}>{Colors.ENDC} {msg}: "
