import re
from typing import List, Tuple


from utils import prompt_message_decorator, print_warning


def get_menu_option(max_choice, msg='Please Indicate Your Choice', min_choice=1) -> int:
    while True:
        user_input = input(prompt_message_decorator(msg))
        try:
            idx = int(user_input)
            if not (min_choice <= idx <= max_choice):
                raise ValueError
            return idx - 1
        except ValueError:
            print_warning("Invalid Input! Please Try Again.")


def get_string_options(list_of_vals: List[str], msg='Please Indicate Your Choice', separator=',',
                       max_num_of_choice: int = None, min_num_of_choice: int = 0) -> List[str]:
    list_of_vals = set(list_of_vals)
    while True:
        try:
            user_input = input(prompt_message_decorator(msg if max_num_of_choice == 1 else f'{msg} '
                                                                                           f'(Separate by "{separator}")'))
            user_choices = [s.strip().title() for s in user_input.split(separator)]
            if max_num_of_choice is not None and not (min_num_of_choice <= len(user_choices) <= max_num_of_choice):
                raise ValueError
            for c in user_choices:
                if c not in list_of_vals:
                    raise ValueError
            return user_choices
        except ValueError:
            print_warning("Invalid Input! Please Try Again.")


def get_time(msg='Please Indicate A Time In 24hrs Format (e.g. 07:30)') -> str:
    while True:
        user_input = input(prompt_message_decorator(msg)).strip()
        if re.match(r'([01]?[0-9]|2[0-3]):[0-5][0-9]', user_input):
            return user_input
        else:
            print_warning("Invalid Input! Please Try Again.")


def get_string_input(msg=None, allow_none=False) -> str:
    while True:
        try:
            user_input = input(prompt_message_decorator(msg)).strip()
            if allow_none or len(user_input) != 0:
                return user_input
            else:
                raise ValueError
        except ValueError:
            print_warning("Invalid Input! Please Try Again.")


def get_int_input(msg=None, min_val=0, max_val=float('inf')) -> int:
    while True:
        user_input = input(prompt_message_decorator(msg))
        try:
            idx = int(user_input)
            if not min_val <= idx <= max_val:
                raise ValueError
            return idx
        except ValueError:
            print_warning(f"Invalid Input! Your Input Should Be A Integer From {min_val} to {max_val}.")


def get_confirmation_id(msg="Please Input Your Confirmation ID With Dashes (Case Insensitive)", to_lower_case=True) -> str:
    while True:
        try:
            user_input = input(prompt_message_decorator(msg)).strip().lower()
            if re.match(r"^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$", user_input):
                return user_input
            else:
                raise ValueError
        except ValueError:
            print_warning("Invalid Input! Please Try Again. Confirmation ID Should Be "
                          "36 Characters With Dashes Included.")


def get_time_period(msg_suffix: str = None, precision='seconds') -> int:
    values = []
    seq = ("Days", "Hours", "Minutes", "Seconds")
    if precision == 'second':
        for i in range(4):
            values.append(get_int_input(f'Please Input {seq[i]} To {msg_suffix}'))

        return values[0] * 24 * 60 * 60 + values[1] * 60 * 60 + values[2] * 60 + values[3]
    elif precision == 'minute':
        for i in range(3):
            values.append(get_int_input(f'Please Input {seq[i]} To {msg_suffix}'))

        return values[0] * 24 * 60 + values[1] * 60 + values[2]
    else:
        raise NotImplementedError("Only Precisions Of Second/Minute Are Supported For Now")
