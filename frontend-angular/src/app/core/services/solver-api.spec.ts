import { TestBed } from '@angular/core/testing';

import { SolverApi } from './solver-api';

describe('SolverApi', () => {
  let service: SolverApi;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SolverApi);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
