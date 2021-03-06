from datetime import datetime
from typing import List

from controllers import BaseController
from utils import *


class FacilityBookingController(BaseController):
    def __init__(self, facility_name_list):
        super().__init__()
        self._options = facility_name_list
        self.day_list = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        self.ctrl_list = ['Back', 'Make Another Booking']

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
        start_day = get_string_options(list_of_vals=day_list, msg='Please Indicate The Starting Day',
                                       max_num_of_choice=1)[0]
        start_time = get_time(msg='Please Indicate A Starting Time In 24hrs Format (e.g. 07:30)')
        end_day = get_string_options(list_of_vals=day_list, msg='Please Indicate The Ending Day',
                                     max_num_of_choice=1)[0]
        end_time = get_time(msg='Please Indicate An Ending Time In 24hrs Format (e.g. 08:30)')
        self.handler(facility_name, start_day, end_day, start_time, end_time)
        print_options(self.ctrl_list)
        return get_menu_option(max_choice=len(self.ctrl_list))

    def handler(self, facility_name: str, start_day, end_day, start_time, end_time):
        try:
            if not self._check_booking_interval(start_day, end_day, start_time, end_time):
                raise Exception('Ending Time Cannot Be Prior To Starting Time!')
            book_id = self.book_facility(facility_name, start_day, end_day, start_time, end_time)
            print_message(msg=f'Your Have Successfully Booked {facility_name} from {start_day} {start_time}'
                              f'to {end_day} {end_time}!')
            print_message(f'Please Note Your Booking ID {inline_important_message_decorator(book_id)} For Later Use')
        except Exception as e:
            print_error(f'Booking Failed: {str(e)}')

    def _check_booking_interval(self, start_day, end_day, start_time, end_time) -> bool:
        # TODO
        prefix_check = re.compile(r'Coming')
        is_start_coming = bool(prefix_check.match(start_day))
        is_end_coming = bool(prefix_check.match(end_day))

        if is_start_coming + is_end_coming == 1:
            if is_start_coming:
                return False
            else:
                return True
        else:
            start_idx = self.day_list.index(start_day.split(' ')[-1])
            end_idx = self.day_list.index(end_day.split(' ')[-1])
            if start_idx < end_idx:
                return True
            elif start_idx == end_idx:
                start_hr, start_min = start_time.split(':')
                end_hr, end_min = end_time.split(':')
                return True if (60 * int(start_hr) + int(start_min)) <= (60 * int(end_hr) + int(end_min)) else False
            else:
                return False

    @staticmethod
    def book_facility(facility_name: str, start_day, end_day, start_time, end_time) -> str:
        # TODO
        if facility_name == 'test error':
            raise Exception('Reason to Fail')
        return 'cdf8d93f'.upper()
