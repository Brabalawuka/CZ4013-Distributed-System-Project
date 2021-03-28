from datetime import datetime

from controllers import BaseController
from utils import *
from helpers import *


class FacilityBookingController(BaseController):
    """
    This is the controller to make booking appointments
    """
    def __init__(self, facility_name_list):
        super().__init__()
        self._options = facility_name_list
        self.day_list = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        self.ctrl_list = ['Back To Homepage', 'Make Another Booking']

    @property
    def message(self):
        return 'All Available Facilities Are Listed Below:'

    @property
    def options(self):
        return self._options

    @options.setter
    def options(self, val):
        pass

    def enter(self, *args, **kwargs) -> int:
        self.show_message()
        self.show_options()
        facility_name = get_string_input(f'Please Indicate the Target Facility by Typing '
                                         f'{inline_important_message_decorator("Full Name")}')
        current_day = datetime.today().weekday()
        day_list = self.day_list[current_day:] + [f'Coming {d}' for d in self.day_list[:current_day]]
        print_options(day_list, show_number=False)
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
        """
        This handles the input from the users by logging hint information and make requests to the server
        :param facility_name: name of the chosen facility
        :param start_day: start day of the booking
        :param end_day: end day of the booking (in 24hr format)
        :param start_time: start time of the booking
        :param end_time: end time of the booking (in 24hr format)
        :return:
        """
        try:
            if not self._check_booking_interval(start_day, end_day, start_time, end_time):
                raise Exception('Ending Time Cannot Be Prior To Starting Time!')
            print_message("Booking...")
            book_id = self.book_facility(facility_name, start_day, end_day, start_time, end_time)
            print_message(msg=f'\nYour Have Successfully Booked {facility_name} From {start_day} {start_time} '
                              f'To {end_day} {end_time}!')
            print_message(f'Please Note Your Booking ID {inline_important_message_decorator(book_id)} For Later Use')
        except Exception as e:
            print_error(f'Booking Failed: {str(e)}')

    def _check_booking_interval(self, start_day, end_day, start_time, end_time) -> bool:
        """
        This checks if the starting time and ending time are valid
        :param start_day: start day of the booking
        :param end_day: end day of the booking (in 24hr format)
        :param start_time: start time of the booking
        :param end_time: end time of the booking (in 24hr format)
        :return:
        """
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
        """
        This makes request to the server to book the facility
        :param facility_name: name of the chosen facility
        :param start_day: start day of the booking
        :param end_day: end day of the booking (in 24hr format)
        :param start_time: start time of the booking
        :param end_time: end time of the booking (in 24hr format)
        :return: an unique booking ID
        """
        reply_msg = request(ServiceType.FACILITY_BOOKING, facility_name, start_day, start_time, end_day, end_time)
        if reply_msg.msg_type == MessageType.EXCEPTION:
            raise Exception(reply_msg.error_msg)
        return reply_msg.data[0]
