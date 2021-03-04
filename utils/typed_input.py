from utils import prompt_message_decorator, print_warning


def get_menu_options(max_choice, msg='Please Indicate Your Choice', min_choice=1) -> int:
    while True:
        user_input = input(prompt_message_decorator(msg))
        try:
            idx = int(user_input)
            if not (min_choice <= idx <= max_choice):
                raise ValueError
            return idx - 1
        except ValueError:
            print_warning("Invalid Input! Please Try Again.")
