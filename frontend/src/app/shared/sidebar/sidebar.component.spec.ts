import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SidebarComponent } from './sidebar.component';
import { By } from '@angular/platform-browser';

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SidebarComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should toggle sidebar visibility when toggleSidebar is called', () => {
    // Initial state is false
    expect(component.isSidebarHidden).toBeFalsy();

    // Call toggleSidebar to change the state
    component.toggleSidebar();
    expect(component.isSidebarHidden).toBeTruthy();

    // Call toggleSidebar again to revert the state
    component.toggleSidebar();
    expect(component.isSidebarHidden).toBeFalsy();
  });

  it('should toggle sidebar visibility when button is clicked', () => {
    // Find the button triggering toggleSidebar (update selector if needed)
    const button = fixture.debugElement.query(By.css('button'));

    // Simulate button click to call toggleSidebar
    button.triggerEventHandler('click', null);

    // Check that the sidebar is now hidden
    expect(component.isSidebarHidden).toBeTruthy();

    // Simulate another click to revert the state
    button.triggerEventHandler('click', null);
    expect(component.isSidebarHidden).toBeFalsy();
  });
});
