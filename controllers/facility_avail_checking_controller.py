from datetime import datetime
from typing import List

from controllers import BaseController
from utils import get_menu_option, get_string_options, print_options, print_message


class FacilityAvailCheckingController(BaseController):
    def __init__(self, facility_name_list):
        super().__init__()
        self._options = facility_name_list
        self.day_list = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']

    @property
    def message(self):
        return 'All Facilities Are Listed Below:'

    @property
    def options(self):
        return self._options

    @options.setter
    def options(self, val):
        pass

    def enter(self, *args, **kwargs):
        self.show_message()
        self.show_options()
        facility_name = self.options[get_menu_option(max_choice=len(self.options),
                                                     msg="Please Indicate the Target Facility by Number (e.g. 1)")]
        current_day = datetime.today().weekday()
        day_list = self.day_list[current_day:] + [f'Coming {d}' for d in self.day_list[:current_day]]
        print_options(day_list, ordered=False)
        chosed_days = get_string_options(list_of_vals=day_list, msg='Please Indicate Day(s) to Check')
        self.handler(facility_name, chosed_days)

    def handler(self, facility_name: str, user_choice: List[str]):
        if len(user_choice) == 1:
            displayed_days = user_choice[0]
        else:
            displayed_days = f'{",".join(user_choice[:-1])} and {user_choice[-1]}'
        print_message(f'Checking the Availability of {facility_name} on {displayed_days}...')
        # TODO
        pass
