import sys

from controllers import BaseController, FacilityAvailCheckingController, FacilityAvailCheckingControllerMultiple, \
    FacilityBookingController, FacilityBookingChangingController, FacilityAvailSubscribingController
from utils import *
from helpers import *


class MainPageController(BaseController):
    """
    This is the controller of the main page that the user see on launching. All other controllers are accessed here.
    """
    @property
    def message(self):
        return 'Welcome to Facility Booking System!'

    @property
    def options(self):
        return [
            "Check Single Facility Availability",
            "Check Multiple Facilities Mutual Availability",
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
        """
        This forwards the user to subsequent controllers
        :param user_choice: choice of service
        :return:
        """
        if user_choice == 0:
            FacilityAvailCheckingController(facility_name_list=self.retrieve_facility_name_list()).start()
        elif user_choice == 1:
            FacilityAvailCheckingControllerMultiple(facility_name_list=self.retrieve_facility_name_list()).start()
        elif user_choice == 2:
            FacilityBookingController(facility_name_list=self.retrieve_facility_name_list()).start()
        elif user_choice == 3:
            FacilityBookingChangingController().start()
        elif user_choice == 4:
            FacilityAvailSubscribingController(facility_name_list=self.retrieve_facility_name_list()).start()
        else:
            self.exit()

    @staticmethod
    def retrieve_facility_name_list():
        """
        This retrieves the name list of all facilities from the server
        :return: list of facility names
        """
        try:
            reply_msg = request(service=ServiceType.FACILITY_NAMELIST_CHECKING)
            if reply_msg.msg_type == MessageType.REPLY:
                return reply_msg.data[0]
            else:
                raise Exception(reply_msg.error_msg)
        except Exception as e:
            print_error(f"Server Unavailable: {str(e)}. Please Try Again Later.")
            sys.exit()

    @staticmethod
    def exit():
        """
        Terminate the program
        :return:
        """
        print_message("Thank You for Using Facility Booking System.")
        sys.exit()
