from abc import ABC, abstractmethod
from utils import print_message, print_options


class BaseController(ABC):
    def __init__(self):
        pass

    @property
    @abstractmethod
    def message(self):
        raise NotImplementedError

    @property
    @abstractmethod
    def options(self):
        raise NotImplementedError

    def show_message(self):
        print_message(self.message)

    def show_options(self):
        print_options(self.options)

    def show_unordered_options(self):
        print_options(self.options, ordered=False)

    def start(self, *args, **kwargs):
        while True:
            self.enter(*args, **kwargs)

    @abstractmethod
    def enter(self, *args, **kwargs):
        raise NotImplementedError



