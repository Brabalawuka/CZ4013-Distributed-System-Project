import re
from typing import List, Union


from utils import prompt_message_decorator, print_warning


def get_menu_option(max_choice, msg='Please Indicate Your Choice', min_choice=1,
                    allow_free_choice=False) -> Union[int, str]:
    if allow_free_choice:
        return input(prompt_message_decorator(msg)).strip()

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


