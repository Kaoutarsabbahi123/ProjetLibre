import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  // Configuration avant chaque test
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule], // Importation de HttpClientTestingModule pour simuler les appels HTTP
      providers: [AuthService],
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  // Test de la création du service
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // Test de la méthode login
  it('should return a response when login is called', () => {
    const mockResponse = { token: 'fake-jwt-token' }; // Réponse mockée de l'API

    const credentials = { email: 'test@example.com', password: 'password123' };

    // Appel à la méthode login
    service.login(credentials).subscribe((response) => {
      expect(response).toEqual(mockResponse); // Vérification que la réponse est correcte
    });

    // Vérification de l'appel HTTP
    const req = httpMock.expectOne('http://localhost:8222/auth/login');
    expect(req.request.method).toBe('POST'); // Vérification que la méthode HTTP est POST
    expect(req.request.body).toEqual(credentials); // Vérification que le body contient les bonnes données

    // Simulation de la réponse de l'API
    req.flush(mockResponse); // Envoi de la réponse mockée
  });

  // Test pour gérer une erreur de requête HTTP
  it('should handle error when login fails', () => {
    const errorMessage = 'Invalid credentials';

    const credentials = { email: 'test@example.com', password: 'wrongpassword' };

    // Appel à la méthode login
    service.login(credentials).subscribe(
      () => fail('expected an error, not a success'),
      (error) => {
        expect(error.status).toBe(500); // Vérification que le statut de l'erreur est 500 (interne)
        expect(error.error).toBe(errorMessage); // Vérification du message d'erreur
      }
    );

    // Vérification de l'appel HTTP
    const req = httpMock.expectOne('http://localhost:8222/auth/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(credentials);

    // Simulation de l'erreur de l'API
    req.flush(errorMessage, { status: 500, statusText: 'Internal Server Error' });
  });

  // Vérification que tous les appels HTTP ont été effectués
  afterEach(() => {
    httpMock.verify();
  });
});
