import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToastContainter } from './toast-containter';

describe('ToastContainter', () => {
  let component: ToastContainter;
  let fixture: ComponentFixture<ToastContainter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastContainter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ToastContainter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
