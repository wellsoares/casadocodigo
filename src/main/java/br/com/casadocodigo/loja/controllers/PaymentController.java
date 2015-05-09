package br.com.casadocodigo.loja.controllers;

import java.math.BigDecimal;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import br.com.casadocodigo.loja.models.PaymentData;
import br.com.casadocodigo.loja.models.ShoppingCart;
import br.com.casadocodigo.loja.service.IntegrandoComPagamento;

@Controller
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private ShoppingCart shoppingCart;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value = "checkout", method = RequestMethod.POST)
	public Callable<String> checkout() {
		return () -> {
			BigDecimal total = shoppingCart.getTotal();
			// String uriToPay = "http://localhost:9000/payment";
			String uriToPay = "http://book-payment.herokuapp.com/payment";
			try {
				String response = restTemplate.postForObject(uriToPay,
						new PaymentData(total), String.class);
				System.out.println("RESPONSE: " + response);
				return "redirect:/payment/success";
			} catch (HttpClientErrorException exception) {
				return "redirect:/payment/error";
			}
		};
	}

	//Outra maneira Assíncrona de efetuar o pagamento
//	@RequestMapping(value = "checkout", method = RequestMethod.POST)
//	public DeferredResult<String> checkout() {
//		BigDecimal total = shoppingCart.getTotal();
//		DeferredResult<String> result = new DeferredResult<String>();
//		IntegrandoComPagamento integrandoComPagamento = new IntegrandoComPagamento(
//				result, total, restTemplate);
//		Thread thread = new Thread(integrandoComPagamento);
//		thread.start();
//		return result;
//	}

	// para versões do JAVA inferiores a 8
	// return new Callable<String>() {
	// @Override
	// public String call() throws Exception {
	// BigDecimal total = shoppingCart.getTotal();
	// String uriToPay = "http://localhost:9000/payment";
	// try {
	// String response =
	// restTemplate.postForObject(uriToPay,
	// new PaymentData(total), String.class);
	// return "redirect:/payment/success";
	// } catch (HttpClientErrorException exception) {
	// return "redirect:/payment/error";
	// }
	// }
	// };

}
