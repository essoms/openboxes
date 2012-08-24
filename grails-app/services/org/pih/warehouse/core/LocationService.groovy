package org.pih.warehouse.core;

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pih.warehouse.core.ActivityCode;

class LocationService {
	
	def grailsApplication
	boolean transactional = true
	
	
	List getAllLocations() { 
		return Location.findAllByActive(true);
	}
	
	List getLoginLocations() { 
		return getLoginLocations(null)
	}
	
	
	List getLoginLocations(Integer currentLocationId) { 
		return getLoginLocations(Location.get(currentLocationId))
	}
	
	List getLoginLocations(Location currentLocation) { 
		def locations = []
		def requiredActivities = grailsApplication.config.app.loginLocation.requiredActivities
		if (requiredActivities) { 
			requiredActivities.each { activity ->
				locations += getAllLocations()?.findAll { it.supports(activity) }
			}			
		}
		if (currentLocation) { 
			locations = locations - currentLocation
		}
		return locations
	}
	
	List getDepots() {
		return getAllLocations()?.findAll { it.supports(ActivityCode.MANAGE_INVENTORY) }
	}

	List getNearbyLocations(Location currentLocation) { 
		return Location.findAllByActiveAndLocationGroup(true, currentLocation.locationGroup)
	}
	
	List getExternalLocations() { 
		return getAllLocations()?.findAll { it.supports(ActivityCode.EXTERNAL) } 
	}
	
	List getDispensaries(Location currentLocation) { 
		return getNearbyLocations(currentLocation)?.findAll { it.supports(ActivityCode.RECEIVE_STOCK) && !it.supports(ActivityCode.EXTERNAL) } 
	}
	
	List getLocationsSupportingActivity(ActivityCode activity) { 
		return getAllLocations()?.findAll { it.supports(activity) }
		
	}
	
	List getShipmentOrigins() { 
		return getLocationsSupportingActivity(ActivityCode.SEND_STOCK)
	}
	
	List getShipmentDestinations() {
		return getLocationsSupportingActivity(ActivityCode.RECEIVE_STOCK)
	}

	List getOrderSuppliers(Location currentLocation) {
		return getLocationsSupportingActivity(ActivityCode.FULFILL_ORDER) - currentLocation
	}

	List getRequestOrigins(Location currentLocation) {
		return getLocationsSupportingActivity(ActivityCode.FULFILL_REQUEST)// - currentLocation
	}

	List getRequestDestinations(Location currentLocation) {
		return getLocationsSupportingActivity(ActivityCode.FULFILL_REQUEST)// - currentLocation
	}

	List getTransactionSources(Location currentLocation) { 
		return getLocationsSupportingActivity(ActivityCode.SEND_STOCK) - currentLocation
	}
	
	List getTransactionDestinations(Location currentLocation) { 
		// Always get nearby locations		
		def locations = getNearbyLocations(currentLocation);		
		
		// Get all external locations (if supports external) 
		if (currentLocation.supports(ActivityCode.EXTERNAL)) { 			
			locations += getExternalLocations();			
		}

		// Of those locations remaining, we need to return only locations that can receive stock
		locations = locations.findAll { it.supports(ActivityCode.RECEIVE_STOCK) }
		
		// Remove current location from list
		locations = locations?.unique() - currentLocation

		return locations
		
	}
	
   
}
