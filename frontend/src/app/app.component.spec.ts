import { render } from '@testing-library/angular';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  it('should create the app component', async () => {
    const { fixture } = await render(AppComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should have as title "frontend"', async () => {
    const { fixture } = await render(AppComponent);
    expect(fixture.componentInstance.title).toEqual('frontend');
  });

});
