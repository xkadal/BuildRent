package com.vlad.buildrent.repository;

import com.vlad.buildrent.model.Rental;
import com.vlad.buildrent.model.RentalStatus;
import com.vlad.buildrent.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByClient(User client);

    List<Rental> findByClientOrderByCreatedAtDesc(User client);

    List<Rental> findByStatus(RentalStatus status);

    List<Rental> findAllByOrderByCreatedAtDesc();
}
