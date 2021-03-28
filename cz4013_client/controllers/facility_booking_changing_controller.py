from typing import Union

from controllers import BaseController
from utils import *
from helpers import *


class FacilityBookingChangingController(BaseController):
    def __init__(self):
        super().__init__()
        self._options = [
            'Advance Booking',
            'Postpone Booking',
            'Extend Booking'
        ]
        self.error_msg = dict({
            b"\x00": 'Non-existing Confirmation ID!',
            b'\x01': 'Facility Not Available For the Changed Time!'
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
                change_opiton = get_menu_option(max_choice=len(self._options))
                if change_opiton == 0:
                    postpone = False
                    shift_time = get_time_period(msg_suffix="Advance your booking", precision='minute')
                    return self.change_handler(booking_id, postpone, shift_time)
                if change_opiton == 1:
                    postpone = True
                    shift_time = get_time_period(msg_suffix="Delay Your Booking", precision='minute')
                    return self.change_handler(booking_id, postpone, shift_time)
                if change_opiton == 2:
                    extend_time = get_time_period(msg_suffix="Extend Your Booking", precision='minute')
                    return self.extend_handler(booking_id, extend_time)
            else:
                return ctrl_opt
        except Exception as e:
            print_error(f'{str(e)}')
            return self.end()

    def change_handler(self, booking_id, postpone: bool, shift_time):
        try:
            print_message("Requesting For Change...")
            self.change_booking(booking_id, postpone, shift_time)
            print_message(msg=f'\nBooking Has Been Successfully Updated!')
            return self.end()
        except Exception as e:
            print_error(f'{str(e)}')
            return self.end()

    def extend_handler(self, booking_id, shift_time):
        try:
            print_message("Requesting For Extension...")
            self.extend_booking(booking_id, shift_time)
            print_message(msg=f'\nBooking Has Been Successfully Updated!')
            return self.end()
        except Exception as e:
            print_error(f'{str(e)}')
            return self.end()

    def end(self) -> int:
        print_options(self.ctrl_list_2)
        return get_menu_option(max_choice=len(self.ctrl_list_2))

    def query_booking(self, booking_id: str) -> dict:
        reply_msg = request(ServiceType.FACILITY_BOOKING_CHECKING, booking_id)
        if reply_msg.msg_type == MessageType.EXCEPTION:
            raise Exception(reply_msg.error_msg)
        return dict(
            facility_name=reply_msg.data[0],
            start_day=reply_msg.data[1],
            start_time=reply_msg.data[2],
            end_day=reply_msg.data[3],
            end_time=reply_msg.data[4],
        )

    def change_booking(self, booking_id: str, postpone: bool, shift_time) -> None:
        reply_msg = request(ServiceType.FACILITY_BOOKING_AMENDMENT, booking_id, postpone, shift_time)
        if reply_msg.msg_type == MessageType.EXCEPTION:
            raise Exception(reply_msg.error_msg)


    def extend_booking(self, booking_id: str, shift_time) -> None:
        reply_msg = request(ServiceType.FACILITY_BOOKING_EXTEND, booking_id, shift_time)
        if reply_msg.msg_type == MessageType.EXCEPTION:
            raise Exception(reply_msg.error_msg)
