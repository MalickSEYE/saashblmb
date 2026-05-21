package com.mouride;

import com.mouride.application.dto.MembreDtos;
import com.mouride.application.usecase.MembreService;
import com.mouride.domain.model.Membre;
import com.mouride.domain.repository.DahiraRepository;
import com.mouride.domain.repository.MembreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests MembreService")
class MembreServiceTest {

    @Mock  MembreRepository membreRepository;
    @Mock  DahiraRepository dahiraRepository;
    @InjectMocks MembreService membreService;

    private Membre membreExistant;

    @BeforeEach
    void setUp() {
        membreExistant = new Membre();
        membreExistant.setId(UUID.randomUUID());
        membreExistant.setNumeroMembre("MR-2024-01001");
        membreExistant.setNom("Diallo");
        membreExistant.setPrenom("Amadou");
        membreExistant.setEmail("amadou@mouride.sn");
        membreExistant.setStatut(Membre.Statut.ACTIF);
    }

    @Test
    @DisplayName("Créer un membre génère un numéro unique")
    void creerMembre_generereNumero() {
        MembreDtos.Request req = MembreDtos.Request.builder()
            .nom("Mbaye").prenom("Fatou").email("fatou@mouride.sn").build();

        when(membreRepository.save(any(Membre.class))).thenAnswer(inv -> {
            Membre m = inv.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        MembreDtos.Response resp = membreService.creer(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getNumeroMembre()).startsWith("MR-");
        assertThat(resp.getNom()).isEqualTo("Mbaye");
        verify(membreRepository, times(1)).save(any(Membre.class));
    }

    @Test
    @DisplayName("Trouver un membre par ID existant")
    void findById_existant() {
        when(membreRepository.findById(membreExistant.getId()))
            .thenReturn(Optional.of(membreExistant));

        MembreDtos.Response resp = membreService.findById(membreExistant.getId());

        assertThat(resp).isNotNull();
        assertThat(resp.getNom()).isEqualTo("Diallo");
        assertThat(resp.getPrenom()).isEqualTo("Amadou");
    }

    @Test
    @DisplayName("Trouver un membre inexistant lève une exception")
    void findById_inexistant_leveException() {
        UUID idInexistant = UUID.randomUUID();
        when(membreRepository.findById(idInexistant)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> membreService.findById(idInexistant))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Membre introuvable");
    }

    @Test
    @DisplayName("Désactiver un membre change son statut à INACTIF")
    void desactiverMembre_changerStatut() {
        when(membreRepository.findById(membreExistant.getId()))
            .thenReturn(Optional.of(membreExistant));
        when(membreRepository.save(any(Membre.class))).thenReturn(membreExistant);

        membreService.desactiver(membreExistant.getId());

        assertThat(membreExistant.getStatut()).isEqualTo(Membre.Statut.INACTIF);
        verify(membreRepository).save(membreExistant);
    }

    @Test
    @DisplayName("Les statistiques membres retournent les bons totaux")
    void getStats_retourneTotaux() {
        when(membreRepository.count()).thenReturn(100L);
        when(membreRepository.countByStatut(Membre.Statut.ACTIF)).thenReturn(80L);
        when(membreRepository.countByStatut(Membre.Statut.INACTIF)).thenReturn(10L);
        when(membreRepository.countByStatut(Membre.Statut.EN_ATTENTE)).thenReturn(8L);
        when(membreRepository.countByStatut(Membre.Statut.SUSPENDU)).thenReturn(2L);

        MembreDtos.Stats stats = membreService.getStats();

        assertThat(stats.getTotal()).isEqualTo(100L);
        assertThat(stats.getActifs()).isEqualTo(80L);
        assertThat(stats.getInactifs()).isEqualTo(10L);
        assertThat(stats.getEnAttente()).isEqualTo(8L);
    }
}
