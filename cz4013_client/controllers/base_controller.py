from abc import ABC, abstractmethod
from utils import print_message, print_options


class BaseController(ABC):
    def __init__(self):
        pass

    @property
    def message(self):
        raise NotImplementedError

    @property
    def options(self):
        raise NotImplementedError

    def show_message(self):
        print_message(self.message)

    def show_options(self):
        print_options(self.options)

    def show_unordered_options(self):
        print_options(self.options, show_number=False)

    def start(self, *args, **kwargs):
        while True:
            i = self.enter(*args, **kwargs)
            if i == 0:
                return

    @abstractmethod
    def enter(self, *args, **kwargs):
        raise NotImplementedError



