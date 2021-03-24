import sys

from controllers import BaseController, FacilityAvailCheckingController, \
    FacilityBookingController, FacilityBookingChangingController, FacilityAvailSubscribingController
from utils import *
from helpers import *


class MainPageController(BaseController):
    @property
    def message(self):
        return 'Welcome to Facility Booking System!'

    @property
    def options(self):
        return [
            "Check Facility Availability",
            "Book Facility",
            "Query/Edit Upcoming Bookings",
            "Monitor Facility Availability",
            "Exit"
        ]

    def enter(self, *args, **kwargs):
        self.show_message()
        self.show_options()
        self.handler(get_menu_option(max_choice=len(self.options)))

    def handler(self, user_choice):
        if user_choice == 0:
            FacilityAvailCheckingController(facility_name_list=self.retrieve_facility_name_list()).start()
        elif user_choice == 1:
            FacilityBookingController(facility_name_list=self.retrieve_facility_name_list()).start()
        elif user_choice == 2:
            FacilityBookingChangingController().start()
        elif user_choice == 3:
            FacilityAvailSubscribingController(facility_name_list=self.retrieve_facility_name_list()).start()
        else:
            self.exit()

    @staticmethod
    def retrieve_facility_name_list():
        # TODO uncomment for connection to server
        try:
            reply_msg = request(service=ServiceType.FACILITY_NAMELIST_CHECKING)
            if reply_msg.msg_type == MessageType.REPLY:
                return reply_msg.data[0]
            else:
                raise Exception(reply_msg.error_msg)
        except Exception as e:
            print_error(f"Server Unavailable: {str(e)}. Please Try Again Later.")
            sys.exit()

        # return ['SRC Swimming Pool', 'North Hill Gym', 'SRC Tennis Court']

    @staticmethod
    def exit():
        print_message("Thank You for Using Facility Booking System.")
        sys.exit()
