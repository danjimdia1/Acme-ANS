
package acme.features.airlineManager.dashboard;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.airport.Airport;
import acme.entities.legs.Leg;
import acme.entities.legs.LegStatus;
import acme.forms.AirlineManagerDashboard;
import acme.realms.airlineManager.AirlineManager;

@GuiService
public class AirlineManagerDashboardShowService extends AbstractGuiService<AirlineManager, AirlineManagerDashboard> {

	@Autowired
	private AirlineManagerDashboardRepository repository;


	@Override
	public void authorise() {
		boolean status;

		status = super.getRequest().getPrincipal().hasRealmOfType(AirlineManager.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		AirlineManagerDashboard dashboard = new AirlineManagerDashboard();

		int airlineManagerId = super.getRequest().getPrincipal().getActiveRealm().getId();
		AirlineManager airlineManager = this.repository.findAirlineManagerById(airlineManagerId);

		Integer ranking = this.repository.calculateRanking(airlineManagerId);
		dashboard.setRanking(ranking);

		Date birthDate = airlineManager.getBirthDate();
		Date currentDate = MomentHelper.getCurrentMoment();

		Duration duration = MomentHelper.computeDuration(birthDate, currentDate);
		long years = duration.toDays() / 365;

		int yearsToRetire = (int) (65 - years);
		dashboard.setYearsToRetire(yearsToRetire);

		int onTimeFlights = this.repository.countLegsByStatus(airlineManagerId, LegStatus.ON_TIME);
		int delayedFlights = this.repository.countLegsByStatus(airlineManagerId, LegStatus.DELAYED);
		double ratioOnTimeDelayedFlights = (double) onTimeFlights / (onTimeFlights + delayedFlights);
		dashboard.setRatioOnTimeDelayedFlights(ratioOnTimeDelayedFlights);

		Collection<Leg> legs = this.repository.findLegsByManagerId(airlineManagerId);

		Map<Airport, Long> airportCounts = new HashMap<>();

		for (Leg leg : legs) {
			airportCounts.merge(leg.getDepartureAirport(), 1L, Long::sum);
			airportCounts.merge(leg.getArrivalAirport(), 1L, Long::sum);
		}

		Airport mostPopular = airportCounts.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

		Airport lessPopular = airportCounts.entrySet().stream().min(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);

		dashboard.setMostPopularAirport(mostPopular);
		dashboard.setLessPopularAirport(lessPopular);

		Map<LegStatus, Integer> legsByStatus = new HashMap<>();

		for (Leg leg : legs) {
			LegStatus status = leg.getStatus();
			legsByStatus.put(status, legsByStatus.getOrDefault(status, 0) + 1);
		}

		dashboard.setLegsByStatus(legsByStatus);

		List<String> currencies = this.repository.findCurrenciesByManagerId(airlineManagerId);

		Map<String, Double> averageFlightCost = new HashMap<>();
		Map<String, Double> minimumFlightCost = new HashMap<>();
		Map<String, Double> maximumFlightCost = new HashMap<>();
		Map<String, Double> standardDeviationFlightCost = new HashMap<>();

		for (String currency : currencies) {
			Double avg = this.repository.avgFlightCostByCurrency(currency, airlineManagerId);
			Double min = this.repository.minFlightCostByCurrency(currency, airlineManagerId);
			Double max = this.repository.maxFlightCostByCurrency(currency, airlineManagerId);
			Double std = this.repository.devFlightCostByCurrency(currency, airlineManagerId);

			if (avg != null && min != null && max != null) {
				averageFlightCost.put(currency, avg);
				minimumFlightCost.put(currency, min);
				maximumFlightCost.put(currency, max);
				standardDeviationFlightCost.put(currency, std);
			}
		}

		dashboard.setAverageFlightCost(averageFlightCost);
		dashboard.setMinimumFlightCost(minimumFlightCost);
		dashboard.setMaximumFlightCost(maximumFlightCost);
		dashboard.setStandardDeviationFlightCost(standardDeviationFlightCost);
		super.getBuffer().addData(dashboard);

	}

	@Override
	public void unbind(final AirlineManagerDashboard object) {
		assert object != null;

		Dataset dataset;

		dataset = super.unbindObject(object, "ranking", "yearsToRetire", "ratioOnTimeDelayedFlights");

		dataset.put("mostPopularAirport", object.getMostPopularAirport() != null ? object.getMostPopularAirport().getIataCode() : "N/A");
		dataset.put("lessPopularAirport", object.getLessPopularAirport() != null ? object.getLessPopularAirport().getIataCode() : "N/A");

		StringBuilder legsStatusString = new StringBuilder();
		for (LegStatus status : object.getLegsByStatus().keySet())
			legsStatusString.append(status).append(": ").append(object.getLegsByStatus().get(status)).append(", ");
		if (!object.getLegsByStatus().isEmpty())
			legsStatusString.setLength(legsStatusString.length() - 2);
		dataset.put("numberofLegsByStatus", legsStatusString.toString());

		StringBuilder avg = new StringBuilder();
		StringBuilder min = new StringBuilder();
		StringBuilder max = new StringBuilder();
		StringBuilder std = new StringBuilder();

		for (String currency : object.getAverageFlightCost().keySet()) {
			Double a = object.getAverageFlightCost().get(currency);
			Double mi = object.getMinimumFlightCost().get(currency);
			Double ma = object.getMaximumFlightCost().get(currency);
			Double s = object.getStandardDeviationFlightCost().get(currency);

			avg.append(currency).append(": ").append(String.format("%.2f", a)).append(", ");
			min.append(currency).append(": ").append(String.format("%.2f", mi)).append(", ");
			max.append(currency).append(": ").append(String.format("%.2f", ma)).append(", ");
			std.append(currency).append(": ").append(s != null ? String.format("%.2f", s) : "N/A").append(", ");
		}

		if (!object.getAverageFlightCost().isEmpty()) {
			avg.setLength(avg.length() - 2);
			min.setLength(min.length() - 2);
			max.setLength(max.length() - 2);
			std.setLength(std.length() - 2);
		}

		dataset.put("averageFlightCost", avg.toString());
		dataset.put("minimumFlightCost", min.toString());
		dataset.put("maximumFlightCost", max.toString());
		dataset.put("deviationFlightCost", std.toString());

		super.getResponse().addData(dataset);

	}

}
