# GermanCurrencyConverter
Demo application showing a self-made currency converter to avoid oracle conversion bugs in ADF 12c.

## Steps to Follow:

1. Set up the database access to a locally available HR Schema.
1. Start the application
1. Enter 9999,99 into the salaray input field and see conversion error.
1. Go to main.jsf in the ViewController project and comment out the <af:convertNumber...> and comment in the f:convertNumber
1. Retry to enter 9999,99 and see that conversion works properly.
