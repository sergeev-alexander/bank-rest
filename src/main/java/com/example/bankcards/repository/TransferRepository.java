package com.example.bankcards.repository;

import com.example.bankcards.entity.Transfer;
import com.example.bankcards.util.specifications.TransferSpecifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long>, JpaSpecificationExecutor<Transfer> {

    default boolean existsByIdAndUserId(Long transferId, Long userId) {
        return exists(TransferSpecifications.hasUserId(userId)
                .and((root, query, cb) -> cb.equal(root.get("id"), transferId)));
    }
}