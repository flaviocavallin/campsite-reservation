package com.campsite.reservations.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.campsite.reservations.domain.CampsiteReservedDate;
import com.campsite.reservations.domain.CampsiteReservedDateId;

@Repository
public interface CampsiteReservedDateRepository extends JpaRepository<CampsiteReservedDate, CampsiteReservedDateId> {

	@Modifying
	@Query("delete from CampsiteReservedDate c where c.campsiteReservedDateId.campsite.id = :campsiteId and c.campsiteReservedDateId.dateReserved between :dateFrom and :dateTo")
	int deleteByCampsiteIdAndBetweenDates(@Param("campsiteId") int campsiteId, @Param("dateFrom") Date dateFrom,
			@Param("dateTo") Date dateTo);

	@Query("select c from CampsiteReservedDate c where c.campsiteReservedDateId.campsite.name = :campsiteName and c.campsiteReservedDateId.dateReserved between :dateFrom and :dateTo")
	List<CampsiteReservedDate> findByCampsiteNameAndBetweenDates(@Param("campsiteName") String campsiteName,
			@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo);

}