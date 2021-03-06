from datetime import datetime
from typing import List

from controllers import BaseController
from utils import *


class FacilityAvailCheckingController(BaseController):
    def __init__(self, facility_name_list):
        super().__init__()
        self._options = facility_name_list
        self.day_list = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        self.ctrl_list = ['Quit', 'Make Another Query']

    @property
    def message(self):
        return 'All Facilities Are Listed Below:'

    @property
    def options(self):
        return self._options

    @options.setter
    def options(self, val):
        pass

    def start(self, *args, **kwargs):
        while True:
            i = self.enter(*args, **kwargs)
            if i == 0:
                return

    def enter(self, *args, **kwargs) -> int:
        self.show_message()
        self.show_options()
        facility_name = self.options[get_menu_option(max_choice=len(self.options),
                                                     msg="Please Indicate the Target Facility by Number (e.g. 1)")]
        current_day = datetime.today().weekday()
        day_list = self.day_list[current_day:] + [f'Coming {d}' for d in self.day_list[:current_day]]
        print_options(day_list, ordered=False)
        chosed_days = get_string_options(list_of_vals=day_list, msg='Please Indicate Day(s) to Check')
        self.handler(facility_name, chosed_days)
        print_options(self.ctrl_list)
        return get_menu_option(max_choice=len(self.ctrl_list))

    def handler(self, facility_name: str, chosen_days: List[str]):
        if len(chosen_days) == 1:
            displayed_days = chosen_days[0]
        else:
            displayed_days = f'{",".join(chosen_days[:-1])} and {chosen_days[-1]}'
        print_message(f'Checking the Availability of {facility_name} on {displayed_days}...')
        avail_by_days = self.retrieve_facility_avail_by_days(facility_name, chosen_days)
        print_timetable(facility_name=facility_name,
                        days=chosen_days,
                        avail_by_days=avail_by_days)

    @staticmethod
    def retrieve_facility_avail_by_days(facility_name: str, chosen_days: List[str]) -> List[str]:
        # TODO
        return ["00:00-13:10;14:50-21:00;21:00-23:59" for _ in chosen_days]
