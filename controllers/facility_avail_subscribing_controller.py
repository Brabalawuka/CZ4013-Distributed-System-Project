# from datetime import datetime
#
# from controllers import FacilityAvailCheckingController
# from utils import *
#
#
# class FacilityAvailSubscribingController(FacilityAvailCheckingController):
#     def __init__(self, facility_name_list):
#         super().__init__(facility_name_list)
#         self.ctrl_list = ['Back To Homepage', 'Make Another Subscription']
#
#     def enter(self, *args, **kwargs) -> int:
#         self.show_message()
#         print_options(self.options, ordered=False)
#         facility_name = get_string_input(f"Please Indicate the Target Facility by Typing Full Name")
#
#         # TODO, get sub time
#         sub_time_in_seconds = 10
#
#         self.handler(facility_name, sub_time_in_seconds)
#         print_options(self.ctrl_list)
#         return get_menu_option(max_choice=len(self.ctrl_list))
#
#     def handler(self, facility_name: str, sub_time_in_seconds: int):
#         print_message(f'Subscribing for the Availability of {facility_name}...')
#         try:
#             self.request_to_subscrption(facility_name, interval=sub_time_in_seconds)
#             try:
#
#         except Exception as e:
#             print_error(f"Bad Request Detected! {str(e)}")
#
#     @staticmethod
#     def request_to_subscrption(facility_name: str, interval: int) -> List[str]:
#         # TODO
#         # raise Exception when facility name does not exist
#         if facility_name == 'test error':
#             raise Exception('Facility Is Not Included In The System!')
#         return ["00:00-13:10;14:50-21:00;21:00-23:59" for _ in chosen_days]
#
#     @classmethod
#     def display_availblity(cls, msg):
#         current_day = datetime.today().weekday()
#         day_list = cls.day_list[current_day:] + [f'Coming {d}' for d in cls.day_list[:current_day]]
