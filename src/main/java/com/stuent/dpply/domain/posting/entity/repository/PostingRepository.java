package com.stuent.dpply.domain.posting.entity.repository;

import com.stuent.dpply.domain.auth.entity.User;
import com.stuent.dpply.domain.posting.entity.Posting;
import com.stuent.dpply.common.enums.PostingStatus;
import com.stuent.dpply.common.enums.PostingTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostingRepository extends JpaRepository<Posting, Long> {

    List<Posting> findByUserAndStatus(User user, PostingStatus status);

    List<Posting> findByStatusOrderByCreateAt(PostingStatus status);

    List<Posting> findByStatusOrderBySympathyCount(PostingStatus status);

    List<Posting> findByTag(PostingTag tag);

    List<Posting> findByUserAndTag(User user, PostingTag tag);

    List<Posting> findByUserAndStatusAndTag(User user, PostingStatus status, PostingTag tag);

    List<Posting> findByUser(User user);

    List<Posting> findAllByStatus(PostingStatus status, Pageable pageable);

    int countByUserAndCreateAtBetween(User user, LocalDate createAt, LocalDate createAt2);
}
