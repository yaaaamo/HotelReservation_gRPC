package com.example.hotelservice.repository;

import com.example.hotelservice.model.Chambre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre, Long> {
  Optional<Chambre> findByNumero(String numero);
}
