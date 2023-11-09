package ewm.utils;

import ewm.category.Category;
import ewm.category.CategoryRepository;
import ewm.event.Event;
import ewm.event.EventRepository;
import ewm.exception.ObjectNotFoundException;
import ewm.user.User;
import ewm.user.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Helper {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static User findUserById(UserRepository repository, Integer id) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Пользователь не найден! Id=%d", id));
        }
        return user.get();
    }

    public static Category findCategoryById(CategoryRepository categoryRepository, int id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Категория не найдена! Id=%d", id));
        }
        return category.get();
    }

    public static Event findEventById(EventRepository eventRepository, long id) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Событие не найдено! Id=%d", id));
        }
        return event.get();
    }
}
