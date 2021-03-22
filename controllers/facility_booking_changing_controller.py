from typing import Union

from controllers import BaseController
from utils import *


class FacilityBookingChangingController(BaseController):
    def __init__(self):
        super().__init__()
        self._options = [
            'Postpone Booking',
            'Advance Booking'
        ]
        self.error_msg = dict({
            b"\x00": 'Non-existing Confirmation ID!',
            b'\x01': 'Facility Not Available For the Shifted Time!'
        })
        self.ctrl_list_1 = ["Back To Homepage", "Edit", "Make Another Query"]
        self.ctrl_list_2 = ['Back To Homepage', 'Make Another Query/Change']

    @property
    def message(self):
        return 'Welcome To Facility Booking Querying/Changing Service!'

    @property
    def options(self):
        return self._options

    def enter(self, *args, **kwargs) -> int:
        self.show_message()
        return self.query_handler(get_confirmation_id())

    def query_handler(self, booking_id: str) -> Union[int, None]:
        try:
            booking_info = self.query_booking(booking_id)
            print_booking(booking_id=booking_id, **booking_info)
            print_options(self.ctrl_list_1)
            ctrl_opt = get_menu_option(max_choice=len(self.ctrl_list_1))
            if ctrl_opt == 1:
                self.show_options()
                forward = bool(get_menu_option(max_choice=len(self._options)))
                shift_time = get_time_period(msg_suffix="Shift", precision='minute')
                return self.change_handler(booking_id, forward, shift_time)
            else:
                return ctrl_opt
        except Exception as e:
            print_error(f'{str(e)}')
            return self.end()

    def change_handler(self, booking_id, forward: bool, shift_time):
        try:
            self.change_booking(booking_id, forward, shift_time)
            print_message(msg=f'Booking Has Been Successfully Updated!')
            return self.end()
        except Exception as e:
            print_error(f'{str(e)}')
            return self.end()

    def end(self) -> int:
        print_options(self.ctrl_list_2)
        return get_menu_option(max_choice=len(self.ctrl_list_2))

    @staticmethod
    def _validate_time(shift_time) -> bool:
        # TODO: check if it is a past time or exceeding this week, could be left to the server side as error handling
        pass

    def query_booking(self, booking_id: str) -> dict:
        # TODO
        if booking_id == '5e0629b5-3a16-4cc5-bd0a-2c09455d3aa7':
            raise Exception(self.error_msg[b'\x00'])
        return {
            'facility_name': 'Hall 14 Gym',
            'start_day': 'Coming Mon',
            'start_time': '11:00',
            'end_day': 'Coming Mon',
            'end_time': '13:00',
        }

    def change_booking(self, booking_id: str, forward: bool, shift_time) -> None:
        # TODO
        if booking_id == '5e0629b5-3a16-4cc5-bd0a-2c09455d3aa8':
            raise Exception(self.error_msg[b'\x00'])
        elif booking_id == '5e0629b5-3a16-4cc5-bd0a-2c09455d3aa9':
            raise Exception(self.error_msg[b'\x01'])
