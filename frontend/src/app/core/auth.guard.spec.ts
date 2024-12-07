import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthGuard } from './auth.guard';

describe('AuthGuard', () => {
  let authGuard: AuthGuard;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(() => {
    // Mock de Router
    mockRouter = {
      createUrlTree: jest.fn(),
    } as unknown as jest.Mocked<Router>;

    // Configuration du test
    TestBed.configureTestingModule({
      providers: [
        AuthGuard,
        { provide: Router, useValue: mockRouter },
      ],
    });

    authGuard = TestBed.inject(AuthGuard);

    // Mock de localStorage
    Object.defineProperty(window, 'localStorage', {
      value: {
        getItem: jest.fn(),
        setItem: jest.fn(),
        removeItem: jest.fn(),
        clear: jest.fn(),
      },
      writable: true,
    });
  });

  it('should allow access if token exists in localStorage', () => {
    // Arrange
    window.localStorage.getItem = jest.fn().mockReturnValue('fake-token'); // Simule un token

    // Act
    const result = authGuard.canActivate();

    // Assert
    expect(result).toBe(true);
  });

  it('should redirect to login if token is not present in localStorage', () => {
    // Arrange
    window.localStorage.getItem = jest.fn().mockReturnValue(null); // Pas de token
    const loginUrlTree = {} as any; // Mock d'UrlTree
    mockRouter.createUrlTree.mockReturnValue(loginUrlTree);

    // Act
    const result = authGuard.canActivate();

    // Assert
    expect(mockRouter.createUrlTree).toHaveBeenCalledWith(['/login']);
    expect(result).toBe(loginUrlTree);
  });
});
