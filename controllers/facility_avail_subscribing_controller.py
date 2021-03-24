from datetime import datetime

from controllers import FacilityAvailCheckingController
from utils import *
from helpers import *


class FacilityAvailSubscribingController(FacilityAvailCheckingController):
    def __init__(self, facility_name_list):
        super().__init__(facility_name_list)
        self.ctrl_list = ['Back To Homepage', 'Make Another Subscription']

    def enter(self, *args, **kwargs) -> int:
        self.show_message()
        print_options(self.options, show_number=False)
        facility_name = get_string_input(f'Please Indicate the Target Facility by Typing '
                                         f'{inline_important_message_decorator("Full Name")}')

        sub_time_in_seconds = get_time_period(msg_suffix="Subscribe", precision='second')

        self.handler(facility_name, sub_time_in_seconds)
        print_options(self.ctrl_list)
        return get_menu_option(max_choice=len(self.ctrl_list))

    def handler(self, facility_name: str, sub_time_in_seconds: int) -> None:
        print_message(f'Subscribing for the Availability of {facility_name}...')
        try:
            request_id = self.request_to_subscrption(facility_name, interval=sub_time_in_seconds)
            print_message(f'You Have Just Successfully Subscribed For The Availability of {facility_name}!')
            print_message(f'If You Would Like To Unsubscribe: Press {inline_important_message_decorator("Ctrl + C")} ')
            try:
                listen(func=self.display_availblity, subscribe_time=sub_time_in_seconds, request_id=request_id)
            except KeyboardInterrupt:
                return
        except Exception as e:
            print_error(f"Subscription Failed! {str(e)}")

    @staticmethod
    def request_to_subscrption(facility_name: str, interval: int) -> str:
        # TODO uncomment for connection to server
        # reply_msg = request(ServiceType.FACILITY_AVAIL_CHECKING_SUBSCRIPTION, facility_name, interval)
        # if reply_msg.msg_type == MessageType.EXCEPTION:
        #     raise Exception(reply_msg.error_msg)
        # return reply_msg.request_id

        return '1'

    @classmethod
    def display_availblity(cls, msg: Union[CallMessage, OneWayMessage, ExceptionMessage]):
        if type(msg) is ExceptionMessage:
            print_error(f'Exception Received From Server: {msg.error_msg}')
        else:
            current_day = datetime.today().weekday()
            day_list = cls.day_list[current_day:] + [f'Coming {d}' for d in cls.day_list[:current_day]]
            print_timetable(days=day_list,
                            avail_by_days=msg.data)
