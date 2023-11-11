package ewm.category;

import ewm.category.dto.CategoryDto;
import ewm.event.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static ewm.utils.Helper.findCategoryById;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, EventRepository eventRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        return CategoryMapper.toDto(categoryRepository.save(CategoryMapper.toEntity(categoryDto)));
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        findCategoryById(categoryRepository, id);
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CategoryDto updateById(int id, CategoryDto categoryDto) {
        Category outdatedCategory = findCategoryById(categoryRepository, id);
        outdatedCategory.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(outdatedCategory);
        return CategoryMapper.toDto(updatedCategory);
    }

    @Override
    public List<CategoryDto> findCategories(Integer from, Integer size) {
        PageRequest pageable = PageRequest.of(from > 0 ? from / size : 0, size);
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.stream().map(CategoryMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto findCategory(int id) {
        Category category = findCategoryById(categoryRepository, id);
        return CategoryMapper.toDto(category);
    }


}
