import { TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService } from '../../core/auth.service';
import { Router } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let authServiceMock: any;
  let routerMock: any;

  beforeEach(() => {
    // Mock d'AuthService
    authServiceMock = {
      login: jest.fn(),
    };

    // Mock de Router
    routerMock = {
      navigate: jest.fn(),
    };

    TestBed.configureTestingModule({
      declarations: [LoginComponent],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
      ],
    }).compileComponents();

    const fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
  });

  it('should call AuthService.login with credentials on form submit', () => {
    const credentials = { email: 'test@example.com', password: 'password123' };
    const responseMock = {
      token: 'test-token',
      user: { id: 1, name: 'Test User' },
    };

    // Mock de la méthode login pour renvoyer une réponse simulée
    authServiceMock.login.mockReturnValue(of(responseMock));

    component.email = credentials.email;
    component.password = credentials.password;

    component.onSubmit();

    expect(authServiceMock.login).toHaveBeenCalledWith(credentials);
  });

  it('should save token and user in localStorage on successful login', () => {
    const responseMock = {
      token: 'test-token',
      user: { id: 1, name: 'Test User' },
    };

    authServiceMock.login.mockReturnValue(of(responseMock));

    component.onSubmit();

    expect(localStorage.getItem('token')).toBe(responseMock.token);
    expect(localStorage.getItem('user')).toBe(JSON.stringify(responseMock.user));
  });

  it('should navigate to /sidebar on successful login', () => {
    const responseMock = {
      token: 'test-token',
      user: { id: 1, name: 'Test User' },
    };

    authServiceMock.login.mockReturnValue(of(responseMock));

    component.onSubmit();

    expect(routerMock.navigate).toHaveBeenCalledWith(['/sidebar']);
  });

  it('should show alert on login error', () => {
    const alertSpy = jest.spyOn(window, 'alert').mockImplementation(() => {});
    authServiceMock.login.mockReturnValue(throwError(() => new Error('Invalid credentials')));

    component.onSubmit();

    expect(alertSpy).toHaveBeenCalledWith('Invalid username or password');
  });
});
