package ewm.category;

import ewm.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto categoryDto);

    void deleteById(int id);

    CategoryDto updateById(int id, CategoryDto categoryDto);

    List<CategoryDto> findCategories(Integer from, Integer size);

    CategoryDto findCategory(int id);
}
