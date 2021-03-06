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


def print_options(opts: List[str], ordered: bool = True, new_line_at_end: bool = False) -> None:
    for i, opt in enumerate(opts, 1):
        print(f"{Colors.OKCYAN}{f'{i} -' if ordered else SpecialCharacters.DOT}{Colors.ENDC} {opt}")
    if new_line_at_end:
        print()


def print_message(msg: str) -> None:
    print(f"{Colors.OKBLUE}{msg}{Colors.ENDC}")


def print_warning(msg: str) -> None:
    print(f"{Colors.WARNING}{msg}{Colors.ENDC}")


def print_error(msg: str) -> None:
    print(f"{Colors.FAIL}{msg}{Colors.ENDC}")


def prompt_message_decorator(msg: str) -> str:
    return f"{Colors.OKBLUE}>{Colors.ENDC} {msg}: "


def inline_important_message_decorator(msg: str) -> str:
    return f"{Colors.OKGREEN}{msg}{Colors.ENDC}"


def print_timetable(facility_name: str, days: List[str], avail_by_days: List[str],
                    new_line_at_top: bool = True, new_line_at_end: bool = True) -> None:
    assert len(days) == len(avail_by_days)
    if new_line_at_top:
        print()
    print_message(f'Available Periods of {facility_name} on the Queried Days')
    max_len_str = max(days, key=len)
    content = []
    for i, v in enumerate(days):
        content.append(f'{v + " " * (len(max_len_str) - len(v))}: {" | ".join(avail_by_days[i].split(";"))}')
    print_options(content, ordered=False)
    if new_line_at_end:
        print()

