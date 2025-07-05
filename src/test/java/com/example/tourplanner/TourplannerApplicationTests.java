package com.example.tourplanner;

import com.example.tourplanner.model.Log;
import com.example.tourplanner.model.Tour;
import com.example.tourplanner.repo.LogRepository;
import com.example.tourplanner.rest.TourController;
import com.example.tourplanner.service.LogService;
import com.example.tourplanner.service.TourService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

//5

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TourplannerApplicationTests {

	@Mock
	private LogRepository logRepository;

	@InjectMocks
	private LogService logService;

	@BeforeEach
	void setUp() {
		logRepository = Mockito.mock(LogRepository.class);
		logService = new LogService(logRepository);
		tourService = mock(TourService.class);
		tourController = new TourController(tourService);
	}

	@Test
	void testCreateLog() {
		Log log = new Log();
		when(logRepository.save(any(Log.class))).thenReturn(log);

		Log result = logService.createLog(log);

		assertNotNull(result);
		verify(logRepository).save(log);
	}

	@Test
	void testGetLogFound() {
		Log log = new Log();
		log.setId(1L);
		when(logRepository.findById(1L)).thenReturn(Optional.of(log));

		Log result = logService.getLog(1L);

		assertEquals(1L, result.getId());
	}


	private TourService tourService;
	private TourController tourController;


	@Test
	public void testGetTourById() {
		Tour tour = new Tour();
		tour.setId(1L);
		tour.setName("Alpen Wanderung");

		when(tourService.getTour(1L)).thenReturn(tour);

		Tour result = tourController.getTour(1L);

		assertEquals(1L, result.getId());
		assertEquals("Alpen Wanderung", result.getName());
		verify(tourService).getTour(1L);
	}

	@Test
	public void testCreateTour() {
		Tour tour = new Tour();
		tour.setName("City Tour");

		Tour savedTour = new Tour();
		savedTour.setId(2L);
		savedTour.setName("City Tour");

		when(tourService.createTour(tour)).thenReturn(savedTour);

		Tour result = tourController.createTour(tour);

		assertEquals(2L, result.getId());
		assertEquals("City Tour", result.getName());
		verify(tourService).createTour(tour);
	}

	@Test
	public void testDeleteTour() {
		when(tourService.deleteTour(1L)).thenReturn(1);  // 1 = success indicator

		int result = tourController.deleteTour(1L);

		assertEquals(1, result);
		verify(tourService).deleteTour(1L);
	}

}