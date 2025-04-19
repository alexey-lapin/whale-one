package com.github.alexeylapin.whaleone.domain.repo;

import com.github.alexeylapin.whaleone.domain.Page;
import com.github.alexeylapin.whaleone.domain.model.AnalysisType;

import java.util.Optional;

public interface AnalysisTypeRepository {

    AnalysisType save(AnalysisType AnalysisType);

    Optional<AnalysisType> findById(long id);

    Page<AnalysisType> findAll(int page, int size);

}
