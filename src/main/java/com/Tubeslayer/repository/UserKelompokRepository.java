package com.Tubeslayer.repository;

import com.Tubeslayer.entity.UserKelompok;
import com.Tubeslayer.entity.id.UserKelompokId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserKelompokRepository extends JpaRepository<UserKelompok, UserKelompokId> {
    
    /**
     * @param idKelompok ID kelompok
     * @return List UserKelompok
     */
    @Query("SELECT uk FROM UserKelompok uk WHERE uk.kelompok.idKelompok = :idKelompok")
    List<UserKelompok> findByKelompok_IdKelompok(@Param("idKelompok") Integer idKelompok);
    
    /**
     * @param idUser ID user
     * @return List UserKelompok
     */
    @Query("SELECT uk FROM UserKelompok uk WHERE uk.user.idUser = :idUser")
    List<UserKelompok> findByUser_IdUser(@Param("idUser") String idUser);
    
    /**
     * @param idKelompok ID kelompok
     * @param role role ("leader")
     * @return UserKelompok ketua
     */
    @Query("SELECT uk FROM UserKelompok uk WHERE uk.kelompok.idKelompok = :idKelompok AND uk.role = :role")
    UserKelompok findByKelompok_IdKelompokAndRole(
        @Param("idKelompok") Integer idKelompok, 
        @Param("role") String role
    );
    
    /**
     * Cari kelompok yang berisi user tertentu untuk tugas tertentu
     * @param idUser ID user
     * @param idTugas ID tugas
     * @return List UserKelompok yang merupakan anggota kelompok dalam tugas tersebut
     */
    @Query("SELECT uk FROM UserKelompok uk " +
           "WHERE uk.user.idUser = :idUser " +
           "AND uk.kelompok.idKelompok IN (" +
               "SELECT tbk.idKelompok FROM TugasBesarKelompok tbk WHERE tbk.idTugas = :idTugas" +
           ")")
    List<UserKelompok> findByUser_IdUserAndKelompok_InTugaBesar(@Param("idUser") String idUser, 
                                                                 @Param("idTugas") Integer idTugas);
}