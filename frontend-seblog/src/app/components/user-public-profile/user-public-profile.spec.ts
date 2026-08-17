import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserPublicProfile } from './user-public-profile';

describe('UserPublicProfile', () => {
  let component: UserPublicProfile;
  let fixture: ComponentFixture<UserPublicProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserPublicProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserPublicProfile);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
