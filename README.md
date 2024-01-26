# microservices-saga-pattern
 springboot microservices using saga design pattern for distributed transactions.

# flow
order ms <-> payment ms <-> stock ms <-> delivery ms

# order ms
create order -> publish event for payment -> consume event from payment

# payment ms
consume event from order -> create payment -> publish event for stock -> consume event from stock -> publish event for order

# stock ms
consume event from payment -> modify stock -> publish event for delivery -> publish event for payment

# delivery ms
consume event from stock -> create delivery -> publish event for stock
