package org.pih.warehouse.shipping

import java.util.Date;

import org.pih.warehouse.order.OrderShipment;
import org.pih.warehouse.product.Product;
import org.pih.warehouse.receiving.ReceiptItem;
import org.pih.warehouse.core.Person;
import org.pih.warehouse.donation.Donor;
import org.pih.warehouse.inventory.InventoryItem;

class ShipmentItem implements Comparable, java.io.Serializable {

	//def beforeDelete = {
	//	container.removeFromShipmentItems(this)
	//}

	String id
	String lotNumber			// Loose coupling to the inventory lot 
	Date expirationDate			
	Product product		    	// Specific product that we're tracking
	Integer quantity		    // Quantity could be a class on its own				
	Person recipient 			// Recipient of an item
	Donor donor					// Organization that donated the goods
	Date dateCreated;
	Date lastUpdated;
	InventoryItem inventoryItem
	Container container				// 
	//PackageType packageType		// The type of packaging that this item is stored 
									// within.  This is different from the container type  
									// (which might be a pallet or shipping container), in  
									// that this will likely be a box that the item is 
									// actually contained within.
	
	static belongsTo = [ shipment : Shipment ]
	
	static hasMany = [ orderShipments : OrderShipment ]
	
	static hasOne = [receiptItem: ReceiptItem]
	
	
	static mapping = {
		id generator: 'uuid'
	}
	
	//static belongsTo = [ container : Container ] // + shipment : Shipment
	static constraints = {
		container(nullable:true)
		product(blank:false, nullable:false)  // TODO: this doesn't seem to prevent the product field from being empty
		lotNumber(nullable:true, maxSize: 255)
		expirationDate(nullable:true)
		quantity(min:0, blank:false, range: 0..2147483646)
		recipient(nullable:true)
		inventoryItem(nullable:true)
		receiptItem(nullable:true)
		donor(nullable:true)
	}
    

	def orderItems() {
		return orderShipments.collect{it.orderItem}
	}

	
	def totalQuantityShipped() {
		int totalQuantityShipped = 0
		// Should use inventory item instead of comparing product & lot number
		if (shipment.shipmentItems) {
			shipment.shipmentItems.each {
				if (it.product == this.product && it.lotNumber == this.lotNumber) {
					totalQuantityShipped += it.quantity
				}
			}
		}
		return totalQuantityShipped
	}

	def totalQuantityReceived() {
		int totalQuantityReceived = 0
		// Should use inventory item instead of comparing product & lot number
		if (shipment.receipt) { 
			shipment.receipt.receiptItems.each {
				if (it.product == this.product && it.lotNumber == this.lotNumber) {
					totalQuantityReceived += it.quantityReceived
				}
			}
		}
		return totalQuantityReceived
	}
	
	/*
	List addToOrderShipments(OrderShipment orderShipment) {
		OrderShipment.link(orderShipment, this)
		return orderShipments()
	}

	List removeFromOrderShipments(OrderShipment orderShipment) {
		OrderShipment.unlink(orderShipment, this)
		return orderShipments()
	}
	*/
	
	
	/**
	 * Sorts shipping items by associated product name, then lot number, then quantity,
	 * and finally by id. 
	 */
	int compareTo(obj) { 
		def sortOrder = 
			container?.sortOrder <=> obj?.container?.sortOrder ?:
				product?.name <=> obj?.product.name ?: 
					lotNumber <=> obj?.lotNumber ?:
						quantity <=> obj?.quantity ?:
							id <=> obj?.id
		return sortOrder;
		/*
		if (!product?.name && obj?.product?.name) {
			return -1
		}
		else if (!obj?.product?.name && product?.name) {
			return 1
		}
		else {
			if (product?.name <=> obj?.product?.name != 0) {
				return product.name <=> obj.product.name
			}
			else {
				if (!lotNumber && obj?.lotNumber) {
					return -1
				}
				else if (!obj.lotNumber && lotNumber) {
					return 1
				}
				else if (lotNumber <=> obj?.lotNumber != 0) {
					return lotNumber <=> obj.lotNumber
				}
				else {
					if (!quantity && obj?.quantity) {
						return -1
					}
					else if (!obj.quantity && quantity) {
						return 1
					}
					else if (quantity <=> obj?.quantity != 0) {
						return quantity <=> obj.quantity
					}
					else {
						return id <=> obj.id
					}
				}
			}
		}
		*/
	}
	
	ShipmentItem cloneShipmentItem() {
		return new ShipmentItem(
			lotNumber: this.lotNumber, 
			expirationDate: this.expirationDate,
			product: this.product,
			quantity: this.quantity,				
			recipient: this.recipient,
			donor: this.donor,
			container: this.container
		)
	}
}
