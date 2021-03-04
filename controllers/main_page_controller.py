import sys

from controllers.base_controller import BaseController
from utils import get_menu_options, print_message


class MainPageController(BaseController):
    @property
    def message(self):
        return 'Welcome to Facility Booking System!'

    @property
    def options(self):
        return [
            "Check Facility Availability",
            "Book Facility",
            "Edit Upcoming Bookings",
            "Monitor Facility Availability",
            "Exit"
        ]

    def enter(self, *args, **kwargs):
        self.show_message()
        self.show_options()
        self.handler(get_menu_options(max_choice=len(self.options)))

    def handler(self, user_choice):
        if user_choice == 0:
            pass
        elif user_choice == 1:
            pass
        elif user_choice == 2:
            pass
        elif user_choice == 3:
            pass
        else:
            self.exit()

    @staticmethod
    def exit():
        print_message("Thank You For Using Facility Booking System.")
        sys.exit()
