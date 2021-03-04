from typing import List


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
            user_choices = user_input.split(separator)
            if max_num_of_choice is not None and not (min_num_of_choice <= len(user_choices) <= max_num_of_choice):
                raise ValueError
            for c in user_choices:
                if c not in list_of_vals:
                    raise ValueError
            return user_choices
        except ValueError:
            print_warning("Invalid Input! Please Try Again.")

